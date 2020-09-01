package com.ajd.tryout.javagraphqldataloaderdemo;

import com.ajd.tryout.javagraphqldataloaderdemo.model.entity.Dept;
import com.ajd.tryout.javagraphqldataloaderdemo.model.entity.Employee;
import com.ajd.tryout.javagraphqldataloaderdemo.model.service.DeptService;
import com.ajd.tryout.javagraphqldataloaderdemo.model.service.EmployeeService;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentationOptions;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderOptions;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@SpringBootApplication
public class JavaGraphqlDataloaderDemoApplication {

	@Autowired
	@Qualifier("DeptDataLoader")
	private DataLoader deptDataLoader;

	@Autowired
	@Qualifier("DeptDataLoader")
	private DataLoader employeeDataLoader;

	public static void main(String[] args) {
		SpringApplication.run(JavaGraphqlDataloaderDemoApplication.class, args);
	}


	private DataFetcher<List<Employee>> getFilteredEmployeesDataFetcher(EmployeeService employeeService) {
		return dataFetchingEnvironment -> {
			String gender = dataFetchingEnvironment.getArgument("gender");
			int yoj = dataFetchingEnvironment.getArgument("yoj");
			return employeeService.getFilteredEmployee(gender, yoj);
		};
	}


	private DataFetcher<Dept> getDeptDataFetcher(DeptService deptService) {
		return dataFetchingEnvironment -> {

			Employee emp = dataFetchingEnvironment.getSource();

			DataLoader<Long, Dept> deptDataLoader = dataFetchingEnvironment.getDataLoaderRegistry().getDataLoader("deptDataLoader");
			CompletableFuture<Dept> dept = deptDataLoader.load(emp.getDeptId());
			deptDataLoader.dispatch();
			return dept.get();

			// return deptService.getDeptById(emp.getDeptId());
		};
	}


	private RuntimeWiring buildWiring(EmployeeService employeeService, DeptService deptService) {
		return RuntimeWiring.newRuntimeWiring()
				.type(newTypeWiring("Query")
						.dataFetcher("employees", getFilteredEmployeesDataFetcher(employeeService)))
				.type(newTypeWiring("Employee")
						.dataFetcher("dept", getDeptDataFetcher(deptService)))
				.build();
	}


	private GraphQLSchema buildSchema(EmployeeService employeeService, DeptService deptService) {
		GraphQLSchema graphQLSchema = null;

		try {
			URL url = Resources.getResource("schema.graphqls");
			String sdl = Resources.toString(url, Charsets.UTF_8);
			TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
			RuntimeWiring runtimeWiring = buildWiring(employeeService, deptService);
			SchemaGenerator schemaGenerator = new SchemaGenerator();
			graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return graphQLSchema;
	}


	@Bean
	@Primary
	public GraphQL getGraphQL(EmployeeService employeeService, DeptService deptService) {

		DataLoaderDispatcherInstrumentationOptions options = DataLoaderDispatcherInstrumentationOptions.newOptions().includeStatistics(true);
		DataLoaderDispatcherInstrumentation dispatcherInstrumentation = new DataLoaderDispatcherInstrumentation(options);

		return GraphQL.newGraphQL(buildSchema(employeeService, deptService))
				.instrumentation(dispatcherInstrumentation)
				.build();
	}


	@Bean("DeptDataLoader")
	public DataLoader<Long, Dept> getDeptDataLoader(DeptService deptService) {
		BatchLoader<Long, Dept> deptBatchLoader = new BatchLoader<Long, Dept>() {
			@Override
			public CompletionStage<List<Dept>> load(List<Long> deptIds) {
				return CompletableFuture.supplyAsync(() -> {
					return deptService.getDeptsById(deptIds);
				});
			}
		};

		return DataLoader.newDataLoader(deptBatchLoader, DataLoaderOptions.newOptions().setBatchingEnabled(true));
	}


	@Bean("EmployeeDataLoader")
	public DataLoader<Long, Employee> getEmployeeDataLoader(EmployeeService employeeService) {
		BatchLoader<Long, Employee> employeeBatchLoader = new BatchLoader<Long, Employee>() {
			@Override
			public CompletionStage<List<Employee>> load(List<Long> empIds) {
				return CompletableFuture.supplyAsync(() -> {
					return employeeService.getEmployeesById(empIds);
				});
			}
		};

		return DataLoader.newDataLoader(employeeBatchLoader);
	}


	@Bean
	@Primary
	public DataLoaderRegistry getDataLoaderRegistry() {
		DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
		dataLoaderRegistry.register("deptDataLoader", deptDataLoader);
		dataLoaderRegistry.register("employeeDataLoader", employeeDataLoader);
		return dataLoaderRegistry;
	}
}

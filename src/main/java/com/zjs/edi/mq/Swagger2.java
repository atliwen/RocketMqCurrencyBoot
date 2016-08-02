//package com.zjs.edi.mq;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//@Configuration
//@EnableSwagger2
//public class Swagger2
//{
//
//	@Bean
//	// Bean 创建调用的方法
//	public Docket createRestApi()
//	{
//		// apiInfo()用来创建该Api的基本信息（这些基本信息会展现在文档页面中）
//		// select()函数返回一个ApiSelectorBuilder实例用来控制哪些接口暴露给Swagger来展现，
//		// 选择那些路径和api会生成document
//		// 本例采用指定扫描的包路径来定义，Swagger会扫描该包下所有Controller定义的API，并产生文档内容（除了被@ApiIgnore指定的请求）
//		// 监控那些 api
//		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
//				.apis(RequestHandlerSelectors.basePackage("com.exampl.myproject.web"))
//				.paths(PathSelectors.any()).build();
//
//		// return new Docket(DocumentationType.SWAGGER_2)
//		// .select() // 选择那些路径和api会生成document
//		// .apis(RequestHandlerSelectors.any()) // 对所有api进行监控
//		// .paths(PathSelectors.any()) // 对所有路径进行监控
//		// .build();
//	}
//
//	/**
//	 * 用来创建该Api的基本信息
//	 * @return
//	 */
//	private ApiInfo apiInfo()
//	{
//		return new ApiInfoBuilder().title("Spring Boot中使用Swagger2构建RESTful APIs")
//				.description("更多Spring Boot相关文章请关注：http://blog.didispace.com/")
//				.termsOfServiceUrl("http://blog.didispace.com/").contact("DD").version("1.0")
//				.build();
//	}
//
// }
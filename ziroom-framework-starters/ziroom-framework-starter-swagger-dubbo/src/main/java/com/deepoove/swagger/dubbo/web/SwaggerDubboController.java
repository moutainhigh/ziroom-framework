package com.deepoove.swagger.dubbo.web;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deepoove.swagger.dubbo.config.DubboPropertyConfig;
import com.deepoove.swagger.dubbo.config.DubboServiceScanner;
import com.deepoove.swagger.dubbo.config.SwaggerDocCache;
import com.deepoove.swagger.dubbo.reader.Reader;

import io.swagger.annotations.Api;
import io.swagger.config.SwaggerConfig;
import io.swagger.models.Swagger;
import springfox.documentation.spring.web.json.Json;

@Controller
@RequestMapping("${swagger.dubbo.doc:swagger-dubbo}")
@Api(hidden = true)
public class SwaggerDubboController {

    public static final String DEFAULT_URL = "/api-docs";
    private static final String HAL_MEDIA_TYPE = "application/hal+json";
    
	@Autowired
	private DubboServiceScanner dubboServiceScanner;
	@Autowired
	private DubboPropertyConfig dubboPropertyConfig;
	@Autowired
	private SwaggerDocCache swaggerDocCache;

	@Value("${swagger.dubbo.http:h}")
	private String httpContext;
	@Value("${swagger.dubbo.enable:true}")
	private boolean enable = true;
	@Value("${swagger.servlet.context:}")
	private String servletContext;

	@RequestMapping(value = DEFAULT_URL, 
	        method = RequestMethod.GET, 
	        produces = {"application/json; charset=utf-8", HAL_MEDIA_TYPE})
	@ResponseBody
	public ResponseEntity<Json> getApiList() throws Exception {
		
		if (!enable){
			return new ResponseEntity<Json>(HttpStatus.NOT_FOUND);
		}
		
		Swagger swagger = swaggerDocCache.getSwagger();
		if (null != swagger){
			return new ResponseEntity<Json>(new Json(io.swagger.util.Json.mapper().writeValueAsString(swagger)), HttpStatus.OK);
		}else{
			swagger = new Swagger();
		}

		final SwaggerConfig configurator = dubboPropertyConfig;
		if (configurator != null) {
			configurator.configure(swagger);
		}

		Map<Class<?>, Object> interfaceMapRef = dubboServiceScanner.interfaceMapRef();
		if (null != interfaceMapRef) {
			Reader.read(swagger, interfaceMapRef, getContextPath(servletContext, httpContext));
		}
		swaggerDocCache.setSwagger(swagger);
		return new ResponseEntity<Json>(new Json(io.swagger.util.Json.mapper().writeValueAsString(swagger)), HttpStatus.OK);
	}

	private String getContextPath(String servletContext, String httpContext) {
		if (StringUtils.isNotBlank(servletContext)) {
			return servletContext + "/" + httpContext;
		}
		return httpContext;
	}
}

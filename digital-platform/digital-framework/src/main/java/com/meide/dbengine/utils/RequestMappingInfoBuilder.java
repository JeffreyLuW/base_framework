package com.meide.dbengine.utils;

import org.springframework.lang.Nullable;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

public class RequestMappingInfoBuilder implements RequestMappingInfo.Builder {

    private String[] paths = new String[0];

    private RequestMethod[] methods = new RequestMethod[0];

    private String[] params = new String[0];

    private String[] headers = new String[0];

    private String[] consumes = new String[0];

    private String[] produces = new String[0];

    @Nullable
    private String mappingName;

    @Nullable
    private RequestCondition<?> customCondition;

    private RequestMappingInfo.BuilderConfiguration options = new RequestMappingInfo.BuilderConfiguration();

    public RequestMappingInfoBuilder(String... paths) {
        this.paths = paths;
    }

    @Override
    public RequestMappingInfoBuilder paths(String... paths) {
        this.paths = paths;
        return this;
    }

    @Override
    public RequestMappingInfo.Builder methods(RequestMethod... methods) {
        this.methods = methods;
        return this;
    }

    @Override
    public RequestMappingInfo.Builder params(String... params) {
        this.params = params;
        return this;
    }

    @Override
    public RequestMappingInfo.Builder headers(String... headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public RequestMappingInfo.Builder consumes(String... consumes) {
        this.consumes = consumes;
        return this;
    }

    @Override
    public RequestMappingInfo.Builder produces(String... produces) {
        this.produces = produces;
        return this;
    }

    @Override
    public RequestMappingInfo.Builder mappingName(String name) {
        this.mappingName = name;
        return this;
    }

    @Override
    public RequestMappingInfo.Builder customCondition(RequestCondition<?> condition) {
        this.customCondition = condition;
        return this;
    }

    @Override
    public RequestMappingInfo.Builder options(RequestMappingInfo.BuilderConfiguration options) {
        this.options = options;
        return this;
    }

    @Override
    public RequestMappingInfo build() {
        ContentNegotiationManager manager = this.options.getContentNegotiationManager();

        PatternsRequestCondition patternsCondition = new PatternsRequestCondition(
                this.paths, this.options.getUrlPathHelper(), this.options.getPathMatcher(),
                this.options.useSuffixPatternMatch(), this.options.useTrailingSlashMatch(),
                this.options.getFileExtensions());

        return new RequestMappingInfo(this.mappingName, patternsCondition,
                new RequestMethodsRequestCondition(this.methods),
                new ParamsRequestCondition(this.params),
                new HeadersRequestCondition(this.headers),
                new ConsumesRequestCondition(this.consumes, this.headers),
                new ProducesRequestCondition(this.produces, this.headers, manager),
                this.customCondition);
    }
}

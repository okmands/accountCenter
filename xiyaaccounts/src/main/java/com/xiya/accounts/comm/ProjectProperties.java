package com.xiya.accounts.comm;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "project")
public class ProjectProperties {
	String charset;
}

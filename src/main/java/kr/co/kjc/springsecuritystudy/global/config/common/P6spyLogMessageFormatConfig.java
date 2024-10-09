package kr.co.kjc.springsecuritystudy.global.config.common;

import com.p6spy.engine.spy.P6SpyOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class P6spyLogMessageFormatConfig {

  @PostConstruct
  public void init() {
    P6SpyOptions.getActiveInstance().setLogMessageFormat(P6spySqlFormatConfig.class.getName());
  }

}

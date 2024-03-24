package test;

import pub.ihub.integration.annotation.AutoConfigPostProcessor;
import pub.ihub.integration.boot.BaseConfigEnvironmentPostProcessor;

@AutoConfigPostProcessor
public final class OtherConfigPostProcessor extends BaseConfigEnvironmentPostProcessor {
  @Override
  protected String getActiveProfile() {
    return "other";
  }
}

package com.zooplus.petstore.configs;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"system:properties",
        "classpath:petstore.properties"})
public interface PetstoreConfigs extends Config {

    PetstoreConfigs PLATFORM_CONFIG = ConfigFactory.create(PetstoreConfigs.class);

    @Key("petstore.baseUrl")
    String petstoreBaseUrl();

    @Key("pet.endpoint")
    String petEndpoint();
}

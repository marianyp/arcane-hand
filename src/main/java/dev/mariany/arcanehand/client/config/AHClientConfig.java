package dev.mariany.arcanehand.client.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AHClientConfig {
    public boolean renderFirstPerson = true;
    public boolean renderThirdPerson = true;
}

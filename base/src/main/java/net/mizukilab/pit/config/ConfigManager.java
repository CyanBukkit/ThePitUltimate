package net.mizukilab.pit.config;

import cn.charlotte.pit.ThePit;
import net.mizukilab.pit.util.configuration.Configuration;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class ConfigManager {
    PitGlobalConfig global;
    List<PitWorldConfig> pitConfigs = new ArrayList<>();
    ThePit instance;
    int cursor;
    public ConfigManager(ThePit thePit){
        this.instance = thePit;
    }
    public PitGlobalConfig getGlobal() {
        PitGlobalConfig pitGlobal = new PitGlobalConfig(instance);
        pitGlobal.load();
        return global = pitGlobal;
    }
    public int setCursor(int index){
        this.cursor = index;
        return index;
    }
    public void nextConfig(){
        cursor++;
    }
    public void synchronizeLegacy(){
        PitWorldConfig selectedWorldConfig = getSelectedWorldConfig();
        Validate.notNull(selectedWorldConfig);
        this.instance.setPitConfig(selectedWorldConfig);
        this.instance.setGlobalConfig(global);
    }
    public PitWorldConfig getSelectedWorldConfig() {
        if(!pitConfigs.isEmpty()){
            return pitConfigs.get(cursor%pitConfigs.size());
        }
        try {
            AtomicBoolean atomicBoolean = new AtomicBoolean();
            Stream<Path> walk = Files.walk(instance.getDataFolder().toPath(), FileVisitOption.FOLLOW_LINKS);
            Optional<Path> worlds = walk.filter(i -> {
                File file = i.toFile();
                return file.isDirectory() && file.getName().equals("worlds");
            }).findFirst();
            walk.close();
            worlds.ifPresentOrElse(i -> {
                atomicBoolean.set(true);
                    File file = i.toFile();
                    if(file.exists()) {
                        File[] files = file.listFiles(s -> s.isFile() && s.exists() && s.getName().endsWith(".yml"));
                        if (files != null) {
                            for (File file1 : files) {
                                PitWorldConfig pitWorldConfig1 = new PitWorldConfig(global, instance, file1.getName(), i.toFile().getName());
                                pitWorldConfig1.load();
                                this.pitConfigs.add(pitWorldConfig1);
                            }
                        }
                    } else {
                        file.mkdirs();
                    }
            }, () -> {
                System.out.println("Didn't have any worlds, shutting down");
            });
            boolean b = atomicBoolean.get();
            if (b) {
                if (pitConfigs.isEmpty()) {
                    b = false;
                    System.out.println("Didn't have any worlds, shutting down");
                    return null;
                } else {
                    return pitConfigs.get(0);
                }
            }
        } catch (Exception e){
            return null;
        }
        return null;
    }

    public void save(){
        global.save();
        this.pitConfigs.forEach(Configuration::save);
    }
    public void reload(){
        global.load();
        instance.setGlobalConfig(global);
        pitConfigs.clear();
        cursor = 0;
        PitWorldConfig selectedWorldConfig = this.getSelectedWorldConfig();
        if(selectedWorldConfig != null) {
            instance.setPitConfig(selectedWorldConfig);
        } else {
            System.out.println("Can't find a suitable config for world map");
        }
    }
}

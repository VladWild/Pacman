package com.vladwild.game.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ResourceManager {
    public final String resourceName;

    public ResourceManager() {
        resourceName = null;
    }

    public ResourceManager(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getString(String key) {
        FileHandle propertiesFileHandle = Gdx.files.internal(resourceName);      //читаем файл property
        Properties property = new Properties();
        try {
            property.load(new BufferedInputStream(propertiesFileHandle.read()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return property.getProperty(key);
    }

    public FileHandle getFileHandle(String key) {
        FileHandle propertiesFileHandle = Gdx.files.internal(resourceName);      //читаем файл property
        Properties property = new Properties();
        try {
            property.load(new BufferedInputStream(propertiesFileHandle.read()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Gdx.files.internal(property.getProperty(key));
    }




}

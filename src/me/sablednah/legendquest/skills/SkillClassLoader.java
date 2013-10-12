package me.sablednah.legendquest.skills;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import me.sablednah.legendquest.utils.IOHelper;

public class SkillClassLoader extends ClassLoader
{
    
    private final URL base;
    
    public SkillClassLoader(final URL url)
    {
        this.base = url;
    }
    
    @Override
    public URL getResource(final String name) {
        try {
            return new URL(this.base, name);
        } catch (final MalformedURLException e) {}
        return null;
    }
    
    @Override
    public InputStream getResourceAsStream(final String name) {
        try {
            return new URL(this.base, name).openStream();
        } catch (final IOException e) {}
        return null;
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        Class clazz = findLoadedClass(name);
        if (clazz == null) {
            try {
                return Class.forName(name);
            } catch (final ClassNotFoundException e) {
                clazz = null;
            }
        }
        if (clazz == null) {
            try {
                final InputStream in = getResourceAsStream(name.replace('.', '/') + ".class");
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final byte[] buffer = new byte[IOHelper.BYTE_BUFFER];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                final byte[] bytes = out.toByteArray();
                clazz = defineClass(name, bytes, 0, bytes.length);
                if (resolve) {
                    resolveClass(clazz);
                }
                out.flush();
                out.close();
            } catch (final Exception e) {
                if (clazz == null) {
                    clazz = findSystemClass(name);
                }
                if (clazz == null) {
                    System.out.println("Clazz is still null " + name);
                    super.loadClass(name, resolve);
                }
            }
        }
        return clazz;
    }
}
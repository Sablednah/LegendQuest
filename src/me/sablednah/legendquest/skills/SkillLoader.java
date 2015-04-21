package me.sablednah.legendquest.skills;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import me.sablednah.legendquest.utils.IOHelper;

public class SkillLoader {

	private final File[]	files;

	public SkillLoader(final File[] file) {
		this.files = file;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedList<SkillDefinition> list() {
		final LinkedList defs = new LinkedList();
		for (final File file : this.files) {
			list(file, defs);
		}
		return defs;
	}

	private void list(final File file, final LinkedList<SkillDefinition> defs) {
		if (file != null) {
			if (file.isDirectory()) {
				try {
					final ClassLoader loader = new SkillClassLoader(file.toURI().toURL());
					for (final File item : file.listFiles()) {
						load(item, defs, loader);
					}
				} catch (final IOException localIOException) {
				}
			} else if (IOHelper.isJar(file)) {
				try {
					final ClassLoader ldr = new SkillClassLoader(IOHelper.getJarUrl(file));
					load(ldr, defs, new JarFile(file));
				} catch (final IOException localIOException1) {
				}
			}
		}
	}

	private void load(final ClassLoader loader, final LinkedList<SkillDefinition> skills, final File file, final String prefix) {
		if (file.isDirectory()) {
			if (!file.getName().startsWith(".")) {
				for (final File f : file.listFiles()) {
					load(loader, skills, f, prefix + file.getName() + ".");
				}
			}
		} else {
			String name = prefix + file.getName();
			final String ext = ".class";
			if ((name.endsWith(ext)) && (!name.startsWith(".")) && (!name.contains("!")) && (!name.contains("$"))) {
				name = name.substring(0, name.length() - ".class".length());
				load(loader, skills, name, file.getAbsolutePath());
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void load(final ClassLoader loader, final LinkedList<SkillDefinition> skills, final JarFile jar) {
		final Enumeration entries = jar.entries();
		while (entries.hasMoreElements()) {
			final JarEntry e = (JarEntry) entries.nextElement();
			final String name = e.getName().replace('/', '.');
			final String ext = ".class";
			if ((name.endsWith(ext)) && (!name.contains("$"))) {
				load(loader, skills, name.substring(0, name.length() - ".class".length()), jar.getName());
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void load(final ClassLoader loader, final LinkedList<SkillDefinition> skills, final String name, final String path) {
		Class clazz = null;
		try {
			clazz = loader.loadClass(name);
		} catch (final Exception e) {
			System.out.println(name + " is not a valid skill and was ignored!");
			e.printStackTrace();
			return;
		} catch (final VerifyError e) {
			return;
		}

		if (clazz.isAnnotationPresent(SkillManifest.class)) {
			SkillManifest manifest = (SkillManifest) clazz.getAnnotation(SkillManifest.class);
			if ((manifest.type() == null) || (manifest.author().equalsIgnoreCase("")) || (manifest.name().equalsIgnoreCase(""))) {
				System.out.println(name + " is not a valid skill and was ignored!");
				return;
			}
			SkillDefinition def;
			try {
				def = new SkillDefinition(clazz, this, new SkillInfo(
						manifest.author(), 
						manifest.name(), 
						manifest.description(), 
						manifest.type(), 
						manifest.version(), 
						manifest.buildup(), 
						manifest.delay(), 
						manifest.duration(),
						manifest.cooldown(), 
						manifest.pay(), 
						manifest.xp(), 
						manifest.manaCost(), 
						manifest.consumes(), 
						manifest.levelRequired(), 
						manifest.skillPoints(),
						manifest.dblvarnames(),
						manifest.dblvarvalues(),
						manifest.intvarnames(),
						manifest.intvarvalues(),
						manifest.strvarnames(),
						manifest.strvarvalues(),
						manifest.karmaCost(),
						manifest.karmaReward(),
						manifest.karmaRequired(),
						manifest.needPerm()
						)
				);
			} catch (BadSkillFormat e) {
				System.out.println(name + " error importing skill, check variable pairs!");
				return;
			}
			skills.add(def);
			return;
		}
	}

	public void load(final File file, final LinkedList<SkillDefinition> defs, ClassLoader loader) throws IOException {
		if (IOHelper.isJar(file)) {
			load(new SkillClassLoader(IOHelper.getJarUrl(file)), defs, new JarFile(file));
		} else {
			if (loader == null) {
				loader = new SkillClassLoader(file.getParentFile().toURI().toURL());
			}
			load(loader, defs, file, "");
		}
	}

	public Skill load(final SkillDefinition def) throws InstantiationException, IllegalAccessException {
		if (def.getSkillClass().getSuperclass().equals(Skill.class)) {
			return (Skill) def.getSkillClass().asSubclass(Skill.class).newInstance();
		}
		return null;
	}
}
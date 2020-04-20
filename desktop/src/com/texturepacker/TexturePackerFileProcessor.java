package com.texturepacker;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;
import com.texturepacker.TexturePacker.Settings;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TexturePackerFileProcessor extends FileProcessor {
	private final Settings defaultSettings;
	private ObjectMap<File, Settings> dirToSettings = new ObjectMap();
	private Json json = new Json();
	private String packFileName;
	private File root;
	ArrayList<File> ignoreDirs = new ArrayList();

	public TexturePackerFileProcessor () {
		this(new Settings(), "pack.atlas");
	}

	public TexturePackerFileProcessor (Settings defaultSettings, String packFileName) {
		this.defaultSettings = defaultSettings;

		if (packFileName.indexOf('.') == -1 || packFileName.endsWith(".png") || packFileName.endsWith(".jpg"))
			packFileName += ".atlas";
		this.packFileName = packFileName;

		setFlattenOutput(true);
		addInputSuffix(".png", ".jpg");
	}

	public ArrayList<Entry> process (File inputFile, File outputRoot) throws Exception {
		root = inputFile;

		// Collect pack.json setting files.
		final ArrayList<File> settingsFiles = new ArrayList();
		FileProcessor settingsProcessor = new FileProcessor() {
			protected void processFile (Entry inputFile) throws Exception {
				settingsFiles.add(inputFile.inputFile);
			}
		};
		settingsProcessor.addInputRegex("pack\\.json");
		settingsProcessor.process(inputFile, null);
		// Sort parent first.
		Collections.sort(settingsFiles, new Comparator<File>() {
			public int compare (File file1, File file2) {
				return file1.toString().length() - file2.toString().length();
			}
		});
		for (File settingsFile : settingsFiles) {
			// Find first parent with settings, or use defaults.
			Settings settings = null;
			File parent = settingsFile.getParentFile();
			while (true) {
				if (parent.equals(root)) break;
				parent = parent.getParentFile();
				settings = dirToSettings.get(parent);
				if (settings != null) {
					settings = new Settings(settings);
					break;
				}
			}
			if (settings == null) settings = new Settings(defaultSettings);
			// Merge settings from current directory.
			try {
				json.readFields(settings, new JsonReader().parse(new FileReader(settingsFile)));
			} catch (SerializationException ex) {
				throw new GdxRuntimeException("Error reading settings file: " + settingsFile, ex);
			}
			dirToSettings.put(settingsFile.getParentFile(), settings);
		}

		// Do actual processing.
		return super.process(inputFile, outputRoot);
	}

	public ArrayList<Entry> process (File[] files, File outputRoot) throws Exception {
		// Delete pack file and images.
		if (outputRoot.exists()) {
			new File(outputRoot, packFileName).delete();
			FileProcessor deleteProcessor = new FileProcessor() {
				protected void processFile (Entry inputFile) throws Exception {
					inputFile.inputFile.delete();
				}
			};
			deleteProcessor.setRecursive(false);

			String prefix = packFileName;
			int dotIndex = prefix.lastIndexOf('.');
			if (dotIndex != -1) prefix = prefix.substring(0, dotIndex);
			deleteProcessor.addInputRegex(Pattern.quote(prefix) + "\\d*\\.(png|jpg)");

			deleteProcessor.process(outputRoot, null);
		}
		return super.process(files, outputRoot);
	}

	protected void processDir (Entry inputDir, ArrayList<Entry> files) throws Exception {
		if (ignoreDirs.contains(inputDir.inputFile)) return;

		// Find first parent with settings, or use defaults.
		Settings settings = null;
		File parent = inputDir.inputFile;
		while (true) {
			settings = dirToSettings.get(parent);
			if (settings != null) break;
			if (parent.equals(root)) break;
			parent = parent.getParentFile();
		}
		if (settings == null) settings = defaultSettings;

		if (settings.combineSubdirectories) {
			// Collect all files under subdirectories and ignore subdirectories so they won't be packed twice.
			files = new FileProcessor(this) {
				protected void processDir (Entry entryDir, ArrayList<Entry> files) {
					ignoreDirs.add(entryDir.inputFile);
				}

				protected void processFile (Entry entry) {
					addProcessedFile(entry);
				}
			}.process(inputDir.inputFile, null);
		}

		if (files.isEmpty()) return;

		// Pack.
		System.out.println(inputDir.inputFile.getName());
		TexturePacker packer = new TexturePacker(root, settings);
		for (Entry file : files)
			packer.addImage(file.inputFile);
		packer.pack(inputDir.outputDir, packFileName);
	}
}

package de.static_interface.sinklibrary.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ExtendedLanguageConfiguration extends ConfigurationBase
{
	private File path;
	private YamlConfiguration configuration = new YamlConfiguration();
	private HashMap<String, Object> defaults = new HashMap<String, Object>();
	
	public ExtendedLanguageConfiguration(JavaPlugin plugin)
	{
		this.path = new File(plugin.getDataFolder().getAbsolutePath()+File.separator+"Language.yml");
	}
	
	public void addDefaults(HashMap<String, Object> defaults)
	{
		this.defaults = defaults;
	}
	
	@Override
	public void create()
	{
		if ( !path.exists() )
		{
			try
			{
				if ( !path.getParentFile().exists() ) path.getParentFile().mkdirs();
				path.createNewFile();
				configuration.addDefaults(defaults);
				configuration.save(path);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public YamlConfiguration getYamlConfiguration()
	{
		return configuration;
	}

	@Override
	public void load()
	{
		try
		{
			configuration.load(path);
		}
		catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void save()
	{
		try
		{
			configuration.save(path);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void set(String path, Object value)
	{
		if ( !configuration.isSet(path) )
		{
			configuration.addDefault(path, value);
			defaults.put(path, value);
			return;
		}
		configuration.set(path, value);
	}
	
	public String _(String path)
	{
		return configuration.getString(path);
	}
	
	@Override
	public HashMap<String, Object> getDefaults()
	{
		return defaults;
	}

	@Override
	public File getFile()
	{
		return path;
	}
}

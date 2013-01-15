package me.sablednah.legendquest.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import me.sablednah.legendquest.Main;

public class DebugLog {
	public Main			lq;
	private String		filename	= "LegendQuest.log";
	private FileHandler	fh;
	private LogFormat	lf;
	public Logger		log;

	public DebugLog(Main p) {
		this.lq = p;
		try {
			// get the file name
			String fn = p.getDataFolder() + File.separator + this.filename;
			System.out.print(fn);
			File f = new File(p.getDataFolder(), this.filename);			
			

			// create file handler to link file to log - set format to sensible 1 line format
			this.fh = new FileHandler(f.getPath(), true);
			this.lf = new LogFormat();
			this.fh.setFormatter(lf);

			// build the logger
			this.log = Logger.getLogger("LedgendQuest");

			// clean off any handlers for performance
			this.log.setUseParentHandlers(false);
			for (Handler h : this.log.getHandlers()) {
				this.log.removeHandler(h);
			}
			this.log.addHandler(fh);

			this.log.setLevel(Level.ALL);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDebugMode() {
		this.log.setLevel(Level.ALL);
	}

	public void log(Level level, String msg) {
		this.log.log(level, msg);
	}

	public void fine(String msg) {
		this.log.fine(msg);
	}

	public void info(String msg) {
		this.log.info(msg);
	}

	public void warning(String msg) {
		this.log.warning(msg);
	}

	public void severe(String msg) {
		this.log.severe(msg);
	}

	public void error(String msg) {
		severe(msg);
	}

	private class LogFormat extends Formatter {
		private final SimpleDateFormat	date	= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		private LogFormat() {
		}

		public String format(LogRecord record) {
			StringBuilder b = new StringBuilder();
			Throwable ex = record.getThrown();

			b.append(this.date.format(Long.valueOf(record.getMillis())));
			b.append(" [");
			b.append(record.getLevel().getLocalizedName().toUpperCase());
			b.append("] ");
			b.append(record.getMessage());
			b.append('\n');

			if (ex != null) {
				StringWriter w = new StringWriter();
				ex.printStackTrace(new PrintWriter(w));
				b.append(w);
			}

			return b.toString();
		}
	}

	public void closeLog() {
		this.fh.close();
	}
	
	  public void thrown(String sourceClass, String sourceMethod, Throwable thrown) {
		    this.log.throwing(sourceClass, sourceMethod, thrown);
	  }

}

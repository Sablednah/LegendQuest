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

    private class LogFormat extends Formatter {

        private final SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        private LogFormat() {}

        @Override
        public String format(final LogRecord record) {
            final StringBuilder b = new StringBuilder();
            final Throwable ex = record.getThrown();

            b.append(this.date.format(Long.valueOf(record.getMillis())));
            b.append(" [");
            b.append(record.getLevel().getLocalizedName().toUpperCase());
            b.append("] ");
            b.append(record.getMessage());
            b.append('\n');

            if (ex != null) {
                final StringWriter w = new StringWriter();
                ex.printStackTrace(new PrintWriter(w));
                b.append(w);
            }

            return b.toString();
        }
    }

    public Main lq;
    private final String filename = "LegendQuest.log";
    private FileHandler fh;
    private LogFormat lf;

    public Logger log;

    public DebugLog(final Main p) {
        this.lq = p;
        try {
            // get the file name
            final String fn = p.getDataFolder() + File.separator + this.filename;
            System.out.print(fn);
            final File f = new File(p.getDataFolder(), this.filename);

            // create file handler to link file to log - set format to sensible 1 line format
            this.fh = new FileHandler(f.getPath(), true);
            this.lf = new LogFormat();
            this.fh.setFormatter(lf);

            // build the logger
            this.log = Logger.getLogger("LedgendQuest");

            // clean off any handlers for performance
            this.log.setUseParentHandlers(false);
            for (final Handler h : this.log.getHandlers()) {
                this.log.removeHandler(h);
            }
            this.log.addHandler(fh);

            this.log.setLevel(Level.ALL);

        } catch (final SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void closeLog() {
        this.fh.close();
    }

    public void error(final String msg) {
        severe(msg);
    }

    public void fine(final String msg) {
        this.log.fine(msg);
    }

    public void info(final String msg) {
        this.log.info(msg);
    }

    public void log(final Level level, final String msg) {
        this.log.log(level, msg);
    }

    public void setDebugMode() {
        this.log.setLevel(Level.ALL);
    }

    public void severe(final String msg) {
        this.log.severe(msg);
    }

    public void thrown(final String sourceClass, final String sourceMethod, final Throwable thrown) {
        this.log.throwing(sourceClass, sourceMethod, thrown);
    }

    public void warning(final String msg) {
        this.log.warning(msg);
    }

}

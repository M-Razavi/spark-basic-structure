/**
 * Copyright (c) 2015  . All rights reserved.
 *
 * 
 * Part of project common
 * LastEdit Jul 12, 2017-3:23:02 PM

 * 
 * 
 * @author Mahdi
 * @version 1.0
 * @see     
 */

package util;

import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {
  public enum LogLevel {
    TRACE, DEBUG, INFO, WARN, ERROR
  }

  public static final org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

  public static void log(Exception exception, LogLevel level) {
    log("", exception, level);
  }

  public static void log(String message, Exception exception, LogLevel level) {
    switch (level) {
      case TRACE:
        if (logger.isTraceEnabled()) {
          logger.trace(message);
          logger.trace(exception.getMessage());
          logger.trace(getStackTraceAsString(exception));
        }
        break;

      case DEBUG:
        if (logger.isDebugEnabled()) {
          logger.debug(message);
          logger.debug(exception.getMessage());
          logger.debug(getStackTraceAsString(exception));
        }
        break;

      case INFO:
        if (logger.isInfoEnabled()) {
          logger.info(message);
          logger.info(exception.getMessage());
        }
        break;

      case WARN:
        if (logger.isWarnEnabled()) {
          logger.warn(message);
          logger.warn(exception.getMessage());
        }
        break;

      case ERROR:
        if (logger.isErrorEnabled()) {
          logger.error(message);
          logger.error(exception.getMessage());
          logger.error(getStackTraceAsString(exception));
        }
        break;

      default:
        if (logger.isInfoEnabled()) {
        logger.info(message);
        logger.info(exception.getMessage());
        }
        break;
    }
  }

  public static void log(String message, LogLevel level) {
    switch (level) {
      case TRACE:
        logger.trace(message);
        break;

      case DEBUG:
        logger.debug(message);
        break;

      case INFO:
        logger.info(message);
        break;

      case WARN:
        logger.warn(message);
        break;

      case ERROR:
        logger.error(message);
        break;

      default:
        logger.info(message);
        break;
    }
  }

  public static void log(String message) {
    log(message, LogLevel.INFO);
  }

  public static void log(Exception exception) {
    log(exception, LogLevel.ERROR);
  }

  public static void log(String message, Exception exception) {
    log(exception, LogLevel.ERROR);
  }

  private static String getStackTraceAsString(Exception e) {
    StringWriter expWriter = new StringWriter();
    e.printStackTrace(new PrintWriter(expWriter));
    return expWriter.toString();
  }

}

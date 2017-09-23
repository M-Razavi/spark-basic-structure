/**
 * Copyright (c) 2015 . All rights reserved.
 *
 * 
 * Part of project common LastEdit Jul 12, 2017-3:23:02 PM
 * 
 * 
 * 
 * @author Mahdi
 * @version 1.0
 * @see
 */
package util;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * @author Mahdi
 *
 *         Loggers
 */

public class Loggers {

  public static final Logger defaultLogger = LoggerFactory.getLogger(Logger.class);

  /** The Constant Access (logic based logger). */
  public static final Logger Access = LoggerFactory.getLogger("access");
  
  /** The Constant Application (section based logger). */
  public static final Logger Application = LoggerFactory.getLogger("app");

  /** The Constant Network (section based logger). */
  public static final Logger Network = LoggerFactory.getLogger("net");

  /** The Constant Packet (section based logger). */
  public static final Logger Packet = LoggerFactory.getLogger("pkt");


}

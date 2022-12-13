package ru.burdin.clientbase;

import java.util.logging.Logger;

public class Log {
private  static    Logger log;
private  Log () {
}

public  static  Logger lod  () {
    if (log == null) {
        log = Logger.getLogger("log");
    }
    return  log;
}
}

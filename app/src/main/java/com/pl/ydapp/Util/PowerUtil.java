package com.pl.ydapp.Util;

import java.io.File;
import java.io.FileWriter;

public class PowerUtil {
  public static void power(String id) {
    try {
      FileWriter localFileWriterOn = new FileWriter(new File("/proc/gpiocontrol/set_sam"));
      localFileWriterOn.write(id);
      localFileWriterOn.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

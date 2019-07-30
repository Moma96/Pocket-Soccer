package com.example.myapplication.model;

import java.io.Serializable;

public abstract class Timer extends Active implements Serializable {

    int time;
                ///////ODRADI PAUSE I CONTINUE KAKO TREBA!!!!!
    public Timer(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void timerTicked() {}

    public abstract void finished();

    @Override
    protected synchronized void iterate() {
      ///  try {
           // if (time-- != 0) {
                //timerTicked();
                //sleep(1000);
          //  } else {
        //        finished();
         ///       terminate();
         //   }
       // } catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
    }

    @Override
    public synchronized String toString() {
        return time/60 + ":" + time%60;
    }
}

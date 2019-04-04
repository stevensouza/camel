package com.stevesouza.camel.experiment6;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.SpringBase;

@Aspect
public class MySpringAspect extends SpringBase {

  @Pointcut("profile()")
  public void user_monitor() {
  }

  @Pointcut("profile()")
  public void user_exceptions() {
  }

  //@Pointcut("execution(* *.*(..))")
  //@Pointcut("bean(hellWorld)")
  // note because this is build time weavhing it will only weave classes that are being compiled and so
  // not 3rd party libraries and the jdk. i.e. not java.net..* or org.apache.cxf..*
  // also note that || and && must be used in the java aspect and not AND/OR.
  // note com.stevesouza..* monitors and method of any class under that package
  // org.tempuri..* didn't monitor classes in org.tempuri so I removed the extra '.'
  @Pointcut("within(com.stevesouza..*) || within(org.tempuri.*)")
  public void profile() {
  }

//  @Pointcut(
//          "call(public * java.net.SocketImpl+.*(..)) || " +
//                  "call(public * java.net.ServerSocket+.*(..)) || " +
//                  "call(public * java.net.DatagramSocket+.*(..)) || " +
//                  "call(public * java.net.DatagramSocketImpl+.*(..)) || " +
//                  "call(public * java.net.Socket+.*(..)) || " +
//                  "call(public * java.net.URLConnection+.*(..))"
//  )
//  public void net() {
//  }

}
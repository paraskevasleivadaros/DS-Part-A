#!/bin/bash
xterm &
java -jar Azureus2.jar --ui=console &
cd C:\Users\paras\IdeaProjects\DS-Part-A\src
javac broker.java
java broker 3421

xterm &
java -jar Azureus2.jar --ui=console &
cd C:\Users\p3150090\IdeaProjects\DS-Part-A\src
javac broker.java
java broker 3822

xterm &
java -jar Azureus2.jar --ui=console &
cd C:\Users\p3150090\IdeaProjects\DS-Part-A\src
javac broker.java
java broker 3719

xterm &
java -jar Azureus2.jar --ui=console &
cd C:\Users\p3150090\IdeaProjects\DS-Part-A\src
javac publisher.java
java publisher 1

xterm &
java -jar Azureus2.jar --ui=console &
cd C:\Users\p3150090\IdeaProjects\DS-Part-A\src
javac publisher.java
java publisher 2

xterm &
java -jar Azureus2.jar --ui=console &
cd C:\Users\p3150090\IdeaProjects\DS-Part-A\src
javac consumer.java
java consumer 021
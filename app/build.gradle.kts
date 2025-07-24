plugins {
   java
   application
}

group = "com.bot"
version = "1.0.0"

application {
   mainClass.set("com.bot.Main")
}

repositories {
   mavenCentral()
}

dependencies {
   implementation("net.java.dev.jna:jna:5.14.0")
   testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.withType<JavaCompile> {
   options.encoding = "UTF-8"
}

tasks.test {
   useJUnitPlatform()
}

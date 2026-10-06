module com.derivandi.test {

   requires java.compiler;

   requires org.junit.jupiter.api;

   requires com.derivandi;

   opens com.derivandi to  org.junit.platform.commons;
   opens com.derivandi.dsl to  org.junit.platform.commons;
   opens com.derivandi.javadoc to org.junit.platform.commons;
   opens com.derivandi.type to  org.junit.platform.commons;
   opens com.derivandi.structure to  org.junit.platform.commons;
}
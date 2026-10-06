module documentation {

   exports com.derivandi.builder;

   requires com.derivandi;

   requires org.junit.jupiter.api;
   requires org.apache.commons.lang3;

   requires java.compiler;

   opens com.derivandi.article.apt_loading to org.junit.platform.commons;
   opens com.derivandi.article.consistency_test to org.junit.platform.commons;
   opens com.derivandi.article.meta_model to org.junit.platform.commons;
   opens com.derivandi.builder to org.junit.platform.commons;
   opens com.derivandi.article.intro.validation to org.junit.platform.commons;
   opens com.derivandi.article.intro.generation to org.junit.platform.commons;
}
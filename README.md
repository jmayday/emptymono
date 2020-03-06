![Java CI with Maven](https://github.com/jmayday/emptymono/workflows/Java%20CI%20with%20Maven/badge.svg)


spring-webflux has to bee downgraded in order to make test working:
```$xslt
com.jmayday.emptymono.EntityRepositoryTest.shouldReturnEmptyObjectFor404WithResponseBody
```
spring-webflux >= 5.2.x will cause the test to fail.

Check pom.xml for details
# toby spring 3.1
toby님의 책을 따라하면서 deprecated된 부분을 고쳐가는 중입니다.
commit을 7.2부터 해서 없는 부분도 많을 수 있습니다.

## dependency
dependency가 생각보다 많이 달라 build.gradle 파일을 보면 많이 도움이 될지도...?

### junit
junit 4에서 junit 5로 바뀌면서 hamcrest matcher를 사용하지 않습니다.

### sql
mysql connector가 조금 바뀐 것 같습니다. UserDaoJdbc 내에 다른 점이 있을 수 있습니다.

### JAXB
dependency가 기본적으로 많이 바뀌었습니다. javax.xml.bind이 아니라 jakarta.xml.bind로 바뀌었고, jaxb runtime이 필요합니다.

### lombok
lombok은 그냥 쓰려고 넣었는데 무시해도 됩니다.
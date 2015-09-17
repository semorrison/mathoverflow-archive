> ./sbt run org.mathoverflow.archive.Web

will get you an http server running on port 8090, that responds to requests such as
<http://localhost:8090/question/55693>.

In fact, it's up and running on our EC2 server, so you can see it in action at
<http://ec2.mathoverflow.org:8090/question/55693>.

We need someone to properly format the output. This can probably be achieved purely through templating and CSS.
The relevant files are 

* [question.tmpl.html](src/main/resources/question.tmpl.html)
* [question.css](src/main/resources/question.css)

in `/src/main/resources/`. You may find you need to edit [load-json.js](src/main/resources/load-json.js) or the main
static html file [question.html](src/main/resources/question.html), but hopefully not!

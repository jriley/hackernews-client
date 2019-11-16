# hackernews-client
Simple Hackernews Client 

This is a simplified hacker news client. Utilizing a MVVM pattern. There it a tag named `no-dagger` that shows an example of how to build a Kotlin based application without a dependency injection framework. The current master branch has an implementation that includes how Dagger 2 can be used in a Kotlin based MVVM architecture application.  

The ViewModels are from the lifecycle architecture components holding a repository. The repo is feed by a Retrofit supplied api and Room database local storage. The client fetches the latest ids from an api provided by `hacker-news.firebaseio.com` for three different endpoints new, best and top. These ids are then filtered out by any that may exist in the local database to prevent a repeated call. Then are used to fetch the story data object that contains the url to the story. If there is no url the story will be again filtered out upon response. The story object will then be stored locally in the database by the end point classification that it was pulled from i.e. best, top or new. There is an observable pattern set up through the application layers to provide the data to the view models and to the view by RxJava2.

You should be able to do a full test run and install with the following command on the CLI:  
`./gradlew clean test cAT installDeb`

My apologies that I did not put any effort into the launchers nor animations. So, they are out of the box and rough. This is a simplified version of an app that I have build previously and use regularly. I have removed several of the features from that code base and plumbed the basics together here for you to get a rudimentary idea of the patterns that I like to use. Please let me know if you have any questions about the workflow or logic behind the tests. They are not elaborate but show the set up and execution for the architecture both unit an UAT.

On this branch I have implemented the Jacoco/SonarQube integration. If the server is running just run the following command from the cli.  
`./gradlew clean createDebugCoverageReport jacocoTestReport sonarqube`         

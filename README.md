# UniLife-China
UniLife is a big data project that acquire big data by web crawling from Renren website, store it in database and apply data mining methods then visualize it in a friendly way.
To research each university's campus mood and lifestyle reflected in students' timeliness, first, the program searches some keywords in Renren and get the search results by web crawling. Then it goes through the profile pages (only the open ones, not private ones) of the students who posted these statuses and match information of the school they are attending with their status. 

##Renren website log-in mechanism
To check other people's profile, the first thing is to log-in and we need to imitate that with Java program
To learn the log-in mechanism of Renren, I used Http Analyzer to monitor http requests during log-in behavior.
From the analyzer, it is clear what name-value pairs are sent by post method and to what address.
After getting these information, we can imitate the log-in request to Renren. 

##Some main classes and methods of RenrenAnalyze packet
Renren Analyze package  ——   crawl data from Renren and data analyze
<ul>
<li>CrawlSearchedStatus.java  ——  contains the configuration variables and main method of the program</li>
<li>RenrenSpider.java——make http requests, imitate log-in behavior, crawl pages</li>
<li>bhelper.java——connect with MySQL database, insert and update data efficiently</li>
<li>RenrenStats.java——Process and analyze the big data acquired and generate statistic results</li>
</ul>
SwingGUI package——Add Graphic User Interface to the program, make it more user friendly   
  

##Configuration before crawling
variable setups for the main method
<pre>
  /**txt file path of Renren account username and password to log-in*/
  String accountsFilePath = "D:/Renren/account.txt";
  /**the start page number of crawling target */
  int offset = 9420; 
  /**keyword of timeline statuses to crawl in English*/
  String tableName = "party";
  /**keyword of timeline statuses to crawl in Chinese*/
  String keyword = "聚会"; 
  /**the start proxy host to use*/
  int proxyIndex=9; 
  /**if need proxy host to crawl at beginning*/
  boolean needProxy = false;
  /**pause time (in seconds) after every crawl*/
  double sleepSec = 0.5;
</pre>

##Avoid being blocked
To avoid crawler traps, some methods are taken like below:
· Take a rest before next crawling!       
let the thread "sleep" for a second before next crawl mission
· change host and porter to make http requests       
Every once in a while, change host and porter to continue crawling using free proxy so that certain IP won't be blocked
· Look like an explore       
Change the http request headers to look more like an explore especially the user agent

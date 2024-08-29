**How to run the program ?**  
Ans: If you have Java already in your system, you can skip Step 1.  
    **Step 1**: Install Java  
    To install Java go to ``https://www.oracle.com/java/technologies/downloads/?er=221886``  
    Install JDK-14+ and according to your system operating system(mac or windows).  
    Env Setup:  
Ensure the JAVA_HOME environment variable is set to the JDK installation path.  
Add the bin directory of the JDK to your system's PATH environment variable.

**Step 2**: Compile and run the code.  
Go to the file location where you downloaded both the Java file and input files.
Compile it using this command: ```javac FlowLogTagger.java```  
and run using this command: ```java FlowLogTagger```   
after you run the program you will see a file names ``` output.txt``` generated in the same folder    
The output.txt file will contain both the tag based counts aswell as the port,protocol mappings
You can aslo use an IDE like IntelliJ and load and navigate to the file folder containing this code and run ```FlowLogTagger.java```

**What has been tested?**

I tested the program by altering the log file by adding and deleting a row, by changing the port and protocol combinations to verify the tag count   
Also, I have tested port and protocol counts by altering the combinations in log file

**Assumptions Made**
I hard coded the ```Protocol and its Text version ( 6 for TCP)```in a HashMap to map each other and to use them while counting the tags based on the protocol and port mappings, So this code will work for the protocols that are present in this HashMap.  
If there are new protocols added to the lookup table then those should be added to the ``` protocolMappings``` hashmap in the program.  
Also, the log format shouldn't change as I am using the protocol and dstport based on the indexes from the current log file and from aws documentation   
Also, I assumed that the ```FlowLogData``` file is a txt file, if I had assumed this as a csv file then the program will change in other way  

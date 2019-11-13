# Working with CSV Files

# Work Done
    
1. We're going to use OpenCSV to open, read and write CSV files, so the first
    thing we should do is to add OpenCSV to `build.gradle`:
    ```gradle
    compile "com.opencsv:opencsv:4.0"
    ```
    
1. Once we have add it, we can create the CSVController, in this case we will
    create it in src/main/java/urlshortener/utils/CSVController.java, in this file we
    are going to implement the basic operations with whom we are going to manipulate
    CSV files, by now, only will be a reading function that returns all the lines of
    the CSV path we pass  as a parameter into a List of Strings.
    
1. Create a test witch opens a CSV file witch is in a specific path and tries to
    read it and assert that the data readed is correct.

1. Implement CSV Requests.

# TODO

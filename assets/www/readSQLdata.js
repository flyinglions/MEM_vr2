
    // Wait for Cordova to load
    //
    document.addEventListener("deviceready", onDeviceReady, false);
    
    var db = null;
    var functionQueue = new Queue();
    var typeQueue = new Queue();

    // Create the database, if not exist
    function queryDB(tx)
    {
        /*
        //Create Table Bank_Account
        var sql = 'CREATE TABLE IF NOT EXISTS Bank_Account (Account_Number UNIQUE, Bank, Account_Name, Balance_Available, User_ID)';
        tx.executeSql(sql);
        
        //Create Table Budget_Items
        sql = 'CREATE TABLE IF NOT EXISTS Budget_Items (Item_ID UNIQUE,Category,Budgeted_Amount,Remaining_Amount,User_ID)';
        tx.executeSql(sql);
        
        //Create Table User
        sql = 'CREATE TABLE IF NOT EXISTS User (User_ID,User_Name,Password)';
        tx.executeSql(sql);
        
        //Create Table sms
        sql = 'CREATE TABLE IF NOT EXISTS sms (SMS_ID,Date,Transaction_Type,Amount,Balance_Available,Location,Account_Number)';
        tx.executeSql(sql);
        
        //Create Table Reconciliation
        sql = 'CREATE TABLE IF NOT EXISTS Reconciliation (Transaction_ID,Category,Type,Recon,Account_Number,SMS_ID)';
        tx.executeSql(sql);
         */
        
        var length = functionQueue.getLength();
        
        for(var i=0; i < length; i++)
        {
            //alert();
            var type = typeQueue.dequeue();
            if(type == "SELECT")
            {
                tx.executeSql(functionQueue.dequeue(), [], querySuccess, error);
            }
            else
            {
                tx.executeSql(functionQueue.dequeue());
            }
        }
        
        global_trans = tx;
    }

    // Query the database


    // Transaction error callback
    function error(err) 
    {
        console.log("Error processing SQL: "+err.code);
    }

    // Transaction success callback
    function success() 
    {
        console.log("success on database transaction.");
    }

    // Cordova is ready
    function onDeviceReady() 
    {
        db = window.openDatabase("Database", "1.0", "Flying Lions Database", 10485760);
        
        //Create Table Bank_Account
        functionQueue.enqueue('CREATE TABLE IF NOT EXISTS Bank_Account (Account_Number UNIQUE, Bank, Account_Name, Balance_Available, User_ID)');
        typeQueue.enqueue('CREATE');
        //Create Table Budget_Items
        functionQueue.enqueue('CREATE TABLE IF NOT EXISTS Budget_Items (Item_ID UNIQUE,Category,Budgeted_Amount,Remaining_Amount,User_ID)');
        typeQueue.enqueue('CREATE');
        //Create Table User
        functionQueue.enqueue('CREATE TABLE IF NOT EXISTS User (User_ID,User_Name,Password)');
        typeQueue.enqueue('CREATE');
        //Create Table sms
        functionQueue.enqueue('CREATE TABLE IF NOT EXISTS sms (SMS_ID,Date,Transaction_Type,Amount,Balance_Available,Location,Account_Number)');
        typeQueue.enqueue('CREATE');
        //Create Table Reconciliation
        functionQueue.enqueue('CREATE TABLE IF NOT EXISTS Reconciliation (Transaction_ID,Category,Type,Recon,Account_Number,SMS_ID)');
        typeQueue.enqueue('CREATE');
   
        functionQueue.enqueue('SELECT * FROM User');
        typeQueue.enqueue('SELECT');
        
        checkQueue();
        
        functionQueue.enqueue('INSERT INTO User (User_ID, User_Name, Password) VALUES("3", "Test", "pass")');
        typeQueue.enqueue('INSERT');
        
        checkQueue();
        
        functionQueue.enqueue('SELECT * FROM User');
        typeQueue.enqueue('SELECT');
        
        checkQueue();
    }
    
    function querySuccess(tx, results) 
    {
        var len = results.rows.length;
        //console.log("DEMO table: " + len + " rows found.");
        for (var i=0; i<len; i++)
        {
            alert(results.rows.item(i).User_Name);
            //console.log("Row = " + i + " ID = " + results.rows.item(i).id + " Data =  " + results.rows.item(i).data);
        }
    }
    
    function checkQueue()
    {
        db.transaction(queryDB, error, success);
    }
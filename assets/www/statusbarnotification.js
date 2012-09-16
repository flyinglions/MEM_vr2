/**
 *  
 *	Constructor
 */
var NotificationMessenger = function() { 
}

/**
 * @param title Title of the notification
 * @param body Body of the notification
 */
NotificationMessenger.prototype.notify = function(title, body) {
    return PhoneGap.exec(null, null, 'StatusBarNotification',	'notify', [title, body]);
};

/**
 * Clears the Notification Bar
 */
NotificationMessenger.prototype.clear = function() {
    return PhoneGap.exec(null, null, 'StatusBarNotification', 'clear', []);
};

/**
 * 	Load StatusBarNotification
 * */

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin('StatusBarNotification', new NotificationMessenger());
});

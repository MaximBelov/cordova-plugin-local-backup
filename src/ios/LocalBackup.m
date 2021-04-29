#import "LocalBackup.h"
#import <Cordova/CDVPluginResult.h>

static NSString *const FILE_NAME = @"backupData.json";

@implementation LocalBackup

- (void)pluginInitialize {
    
}

- (void) create:(CDVInvokedUrlCommand*)command {
    
    [self.commandDelegate runInBackground:^{
        
        NSString *filePath = self.getBackupPath;
        NSDictionary *data = [command.arguments objectAtIndex:0];
        
        NSError *writeError = nil;
        // options:NSJSONWritingPrettyPrinted
        NSData *serializedData = [NSJSONSerialization dataWithJSONObject:data options:kNilOptions error:&writeError];
        [serializedData writeToFile:filePath atomically:YES];
        
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
    
}

- (void) read:(CDVInvokedUrlCommand*)command {
    
    [self.commandDelegate runInBackground:^{
        
        NSString *filePath = self.getBackupPath;
        NSFileManager *fileManager = [NSFileManager defaultManager];
        
        CDVPluginResult *pluginResult;
        if ([fileManager fileExistsAtPath:filePath]){
            NSData *fileData = [NSData dataWithContentsOfFile:filePath options:NSDataReadingUncached error:nil];
            NSDictionary *serializedData = [NSJSONSerialization JSONObjectWithData:fileData options:kNilOptions error:nil];
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:serializedData];
        } else {
            NSDictionary *jsonObj = [ [NSDictionary alloc] init];
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:jsonObj];
        }
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
    
}

- (void) remove:(CDVInvokedUrlCommand*)command {
    
    [self.commandDelegate runInBackground:^{
        
        NSString *filePath = self.getBackupPath;
        NSFileManager *fileManager = [NSFileManager defaultManager];
        
        CDVPluginResult *pluginResult = [CDVPluginResult
                                         resultWithStatus:CDVCommandStatus_OK
                                         messageAsBool:[fileManager removeItemAtPath:filePath error:nil]
                                         ];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
    
}

- (void) exists:(CDVInvokedUrlCommand*)command {
    
    [self.commandDelegate runInBackground:^{
        NSString *filePath = self.getBackupPath;
        NSFileManager *fileManager = [NSFileManager defaultManager];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:[fileManager fileExistsAtPath:filePath]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
    
}


-(NSString*) getBackupPath {
    NSString *libPath = NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES)[0];
    NSString *libPathNoSync = [libPath stringByAppendingPathComponent:@"NoCloud"];
    NSString *filePath = [libPathNoSync stringByAppendingPathComponent:FILE_NAME];
    return filePath;
}

@end


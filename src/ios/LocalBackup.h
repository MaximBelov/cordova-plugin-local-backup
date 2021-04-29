#import <Cordova/CDVPlugin.h>

@interface LocalBackup : CDVPlugin
- (void)create:(CDVInvokedUrlCommand *)command;
- (void)read:(CDVInvokedUrlCommand *)command;
- (void)exists:(CDVInvokedUrlCommand *)command;
- (void)remove:(CDVInvokedUrlCommand *)command;
@end

# mail-deleter

This is simple tool to delete old and many of mail in (relatively) short time at `gmail.com`. 
This will move many mail to trash folder. After that, you can empty trash folder at `gmail.com` UI. I guess this tool is available except for gmail too because it uses just IMAP only. (But not tested.) 

## Background

My mailbox (gmail) at office had a large amount of mails at the time.
And I receicved many of mail everyday. They were delivered from cron batch process.

One day my mail usage reached 95% of limitation. and it seemed to reach limitation in a few days.
That is why I wrote this small program. This will move old mail to Trash folder,
100,000 of mails by 18 minutes. After that, I cleared all mail in my Trash folder at gmail web ui.
(this is relatively easy.) Usage went down to 30%. I could create open space for mail.

This is very simple one. Just for practice of kotlin coroutine batch process and small practical use.

I will publish this for someone's some purpose.


Porting to Kotlin from Java, I copied some code from [coroutine example](https://github.com/Kotlin/kotlin-coroutines-examples "coroutine example") . For now I could not understand them ,but I will do it in near future!


# How to use

## Preparation


1. Open `/src/main/kotlin/maildeleter/Main.kt` and set proper value to below items. 
   - `EMAIL` 
   - `PASSWD` 
   - `TARGET_FOLDER` for the folder which have mail to delete.
   - `TRASH_FOLDER` to specify your trash folder. This may differ as your environment or language setting of gmail.
2. Open gmail setting -> `Forwarding and POP/IMAP` (or, go to `https://mail.google.com/mail/u/0/#settings/fwdandpop`) -> change setting to `Enable IMAP`. 
3. Go to `https://myaccount.google.com/lesssecureapps`. Next to "Access for less secure apps," select Turn on , as this document said. `https://support.google.com/accounts/answer/6010255?hl=en` (As you noticed, this application is less secure! Please use this tool at your own risk!)
4. Configure setting in `Main.kt`. Those setting relates the number of mail will be deleted by this tool. Try to find proper setting!
   - `UNIT`, how many mails will be processed in one task.
   - `NUM_OF_TASKS` , number of tasks. 
   - `THREAD_POOL_SIZE`, how many threads will be prepared to process tasks.
   
## Execution

```
> gradlew run
```

After that , `UNIT * NUM_OF_TASKS` of  mail in the `TARGET_FOLDER` will be moved to trash folder. The oldest mail will be moved at first, and newest mail is the last mail to be moved.


## Open source licence

Some kotlin files except under `src/main/kotlin/maildeleter` is licenced by Apache License 2.0. Original is https://github.com/Kotlin/kotlin-coroutines-examples . Some import statement is modified.(Avoid Star Import)


Enjoy!



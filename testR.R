args<-commandArgs(TRUE)
#args <- c("output.txt", "hello", 2, "Me")
write.table(args,args[1],quote = F,row.names = F)

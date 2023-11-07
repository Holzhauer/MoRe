library(ggplot2)
path = "C:/Users/holzhauer/workspace/MORe/logs/pascal"
fileNameRandom = "PascalDistribution_random.csv"
fileNameDensity = "PascalDistribution_density.csv"
fileNameCumulativ = "PascalDistribution_cumulativ.csv"

dataDensity <- read.csv(paste(path, fileNameDensity, sep="/"), header = FALSE, sep = ",", quote = "",
		dec = ".")
pd <- ggplot()
pd <- pd +
		layer(
				data = dataDensity
				,mapping = aes(V1, V2)
				,geom="line", stat = "identity")
pd


dataCumulativ <- read.csv(paste(path, fileNameCumulativ, sep="/"), header = FALSE, sep = ",", quote = "",
		dec = ".")
dataCumulativ <- dataCumulativ[dataCumulativ$V1 < 50,]
pc <- ggplot()
pc <- pc +
		layer(
				data = dataCumulativ
				,mapping = aes(V1, V2)
				,geom="line", stat = "identity")
pc


dataRandom <- read.csv(paste(path, fileNameRandom, sep="/"), header = FALSE, sep = ",", quote = "",
		dec = ".")
pr <- ggplot()
pr <- pr +
		layer(
				data = dataRandom
				,mapping = aes(V2)
				,geom="histogram", binwidth = 1)
pr
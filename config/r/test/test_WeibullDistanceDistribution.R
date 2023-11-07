library(ggplot2)
path = "C:/Users/holzhauer/workspace/MORe/logs/weibull"
fileNameRandom = "WeibullDistribution_random.csv"
fileNameDensity = "WeibullDistribution_density.csv"
fileNameCumulativ = "WeibullDistribution_cumulativ.csv"
fileNameInverse = "WeibullDistribution_inverse.csv"


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
				,geom="histogram")
pr


dataInverse <- read.csv(paste(path, fileNameInverse, sep="/"), header = FALSE, sep = ",", quote = "",
		dec = ".")
pi <- ggplot()
pi <- pi +
		layer(
				data = dataInverse
				,mapping = aes(V1, V2)
				,geom="line", stat = "identity")
pi
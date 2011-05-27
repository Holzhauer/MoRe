
moreClusterAVP <- function(graph, directed, unconnected) {
	cls = clusters(graph, mode="weak")
	
	for (i in 1:cls$no) {
		subg = subgraph(graph, which(cls$membership %in% c(i - 1)) - 1)
		result[i] <- average.path.length(subg, directed, unconnected)
	}
	result
}
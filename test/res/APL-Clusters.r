library(igraph)

file = "C:/Sascha/Workspace/More/test/res/TwoClusters.gml"
g <- read.graph(file, "gml");


cls = clusters(g, mode="weak")

for (i in 1:cls$no) {
	subg = subgraph(g, which(cls$membership %in% c(i - 1)) - 1)
	
	tkplot(subg)
	print("The subgraph's transitivity ( clustering coefficient):")
	print(transitivity(subg, type="global"))

	print("The subgraph's average shortest path:")
	print(average.path.length(subg, directed=TRUE, unconnected=FALSE))
}
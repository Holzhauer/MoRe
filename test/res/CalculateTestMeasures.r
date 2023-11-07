# trends.R
# 
# TODO: Add comment
#
# Author: krebs
###############################################################################

setwd("C:/Sascha/Workspace/RS_SoNetA")

library(igraph)

file = "./test/graphs/SmallWorld02.graphml"
# g <- read.graph(file, "graphml");

## create small-world-graph (2dim, 10 nodes in each dim = 100, 2 neighbours connected, link probability 0.2):
g <- watts.strogatz.game(2, 10, 2, 0.2)
g <- simplify(g, remove.multiple = TRUE, remove.loops = TRUE)

write.graph(g, file, format="graphml")

print("The graph's transitivity ( clustering coefficient):")
transitivity(g, type="global")

print("The graph's average shortest path:")
average.path.length(g, directed=FALSE, unconnected=TRUE)

####

gr <- read.graph(file, format="graphml")
print("The graph's transitivity ( clustering coefficient):")
transitivity(gr, type="global")

print("The graph's average shortest path:")
average.path.length(gr, directed=FALSE, unconnected=TRUE)
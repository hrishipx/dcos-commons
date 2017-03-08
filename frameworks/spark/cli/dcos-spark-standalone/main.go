package main

import (
	"github.com/mesosphere/dcos-commons/cli"
	"gopkg.in/alecthomas/kingpin.v2"
	"log"
)

func main() {
	app, err := cli.NewApp("0.1.0", "Mesosphere", "Provides an example for DC/OS service developers")
	if err != nil {
		log.Fatalf(err.Error())
	}

	cli.HandleCommonFlags(app, "spark-standalone", "Apache Spark Standalone CLI")
	cli.HandleConfigSection(app)
	cli.HandleEndpointsSection(app)
	cli.HandlePlanSection(app)
	cli.HandlePodsSection(app)
	cli.HandleStateSection(app)

	// Omit modname:
	kingpin.MustParse(app.Parse(cli.GetArguments()))
}

architectury {
    common("${property("enabled_platforms")}".split(','))
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modApi("dev.architectury:architectury:${property("architectury_version")}")
    modImplementation("software.bernie.geckolib:geckolib-fabric-1.18:3.0.77")
}

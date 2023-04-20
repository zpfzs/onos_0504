COMPILE_DEPS = CORE_DEPS + JACKSON + NETTY + CLI + [
    "//core/store/serializers:onos-core-serializers",
    "//apps/optical-model:onos-apps-optical-model",
]

java_binary(
    name = "my_binary",
    srcs = ["MyBinary.java"],
    deps = ["@maven//:json"],
)



TEST_DEPS = TEST_ADAPTERS + [
    "//utils/osgi:onlab-osgi-tests",
]

osgi_jar_with_tests(
    test_deps = TEST_DEPS,
    deps = COMPILE_DEPS
)


MODULES=("./docker/Dockerfile-backend")

#print_usage
print_usage() {
    echo "options are:"
    echo "-p to push images default false"
    echo "-m modules separated by commam default are ${ALL_MODULES[@]}"
}

while getopts 'pm:e:h' flag; do
  case "${flag}" in
    p) PUSH='true' ;;
    m) MODULES=(${OPTARG}) ;;
    h) print_usage
        exit 1 ;;
    *) print_usage
       exit 1 ;;
  esac
done

create_image() {
    start=$(date +%s)

    MODULE=$(echo $1 |sed  "s/.*docker\/Dockerfile-//")
    IMAGE_NAME=${MODULE}

    docker build \
      -f $PWD/docker/Dockerfile-backend \
      -t tv-channel-service:1.0.0 \
      ..

    if [ $PUSH = "true" ]; then
        docker tag tv-channel-service:1.0.0 10.10.0.222:5000/tv-channel-service:1.0.0
        docker push 10.10.0.222:5000/tv-channel-service:1.0.0
    fi
    end=(date +%s)
    echo "$IMAGE_NAME took: $( echo "$end - $start" | bc -l )s"
}

for ITEM in "${MODULES[@]}"; do
    echo "creating $ITEM";
    create_image $ITEM
done

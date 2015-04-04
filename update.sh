git pull
play clean compile stage

cd target/universal/stage
sudo env PATH=$PATH play stop
sudo -b env PATH=$PATH ./bin/nilda-docetentacao -DapplyEvolutions.default=true
cd ../../../

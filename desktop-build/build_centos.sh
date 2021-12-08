RPM_VERSION="1.0"

#По необходимости изменить
BACKEND_FOLDER=~/teho/teho-backend
FRONTEND_FOLDER=~/teho/teho-frontend
MAVEN_PATH=~/Software/idea-IC-203.5981.155/plugins/maven/lib/maven3/bin

#Плейсхолдеры из teho.spec
BACKEND_PATTERN="%BACKENDPATH%"
FRONTEND_PATTERN="%FRONTENDPATH%"
VERSION_PATTERN="%VERSION%"
FRONTEND_NAME_PATTERN="%FRONTEND_NAME%"
FRONTEND_VERSION_PATTERN="%FRONTEND_VERSION%"

#Вычисляем название и версию frontend'a чтобы правильно подобрать имя AppImage файла
FRONTEND_NAME=$(jq -r '.build.productName' ${FRONTEND_FOLDER}/package.json)
FRONTEND_VERSION=$(jq -r '.version' ${FRONTEND_FOLDER}/package.json)

#Заменяем плейсхолдеры в teho.spec на реальные значения и сохраняем во временный файл teho_tmp.spec
sed -e "s&${BACKEND_PATTERN}&${BACKEND_FOLDER}&g; s&${FRONTEND_PATTERN}&${FRONTEND_FOLDER}&g; s&${VERSION_PATTERN}&${RPM_VERSION}&g; s&${FRONTEND_NAME_PATTERN}&${FRONTEND_NAME}&g; s&${FRONTEND_VERSION_PATTERN}&${FRONTEND_VERSION}&g;" \
  ${BACKEND_FOLDER}/desktop-build/centos/teho.spec > ${BACKEND_FOLDER}/desktop-build/centos/teho_tmp.spec

echo "Building Electron..."

#Собираем AppImage frontend'a
(cd $FRONTEND_FOLDER && npm run electron-pack)

echo "Building JAR..."

#Собираем JAR-файл бэк-енда
(cd $BACKEND_FOLDER && chmod +x ${MAVEN_PATH}/mvn && ${MAVEN_PATH}/mvn package -DskipTests)

echo "Building RPM..."

#Собираем RPM-пакет на основе временного spec-файла, созданного выше
#Принудительное очищение rpmbuild необходимо для того, чтобы удалить AppImage, права на который только у root т.к. копируется
#в spec-файле он через sudo (иначе ничего не работает)
sudo rm -rf ~/rpmbuild/* && rpmbuild -ba ${BACKEND_FOLDER}/desktop-build/centos/teho_tmp.spec

echo "Finished! Installing..."

#Удаляем существующую версию
sudo yum remove teho -y

#Устанавливаем новую версию
sudo yum install ~/rpmbuild/RPMS/x86_64/teho-${RPM_VERSION}-1.x86_64.rpm -y

#Удаляем временный spec-файл
rm ${BACKEND_FOLDER}/desktop-build/centos/teho_tmp.spec

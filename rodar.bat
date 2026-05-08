@echo off

REM Copia o hibernate.cfg.xml para a pasta out
xcopy /y resources\hibernate.cfg.xml out\

REM Compila
javac -cp ".;lib/*" -d out backend\Animal.java backend\Alerta.java backend\AlertaService.java backend\AnimalService.java backend\AuthService.java backend\Backend.java backend\Colar.java backend\ColarService.java backend\Fazenda.java backend\FazendaService.java backend\GeofenceService.java backend\Localizacao.java backend\Lote.java backend\RastreamentoService.java backend\HibernateUtil.java backend\Usuario.java frontend\frontend.java

REM Roda
java -cp ".;out;lib/*" frontend.frontend

pause
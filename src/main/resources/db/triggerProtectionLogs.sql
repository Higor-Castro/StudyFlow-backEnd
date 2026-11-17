CREATE OR REPLACE FUNCTION bloquear_alteracao_logs()
RETURNS TRIGGER AS $$
BEGIN
 IF TG_OP = 'DELETE' THEN
 RAISE EXCEPTION 'Exclusao de logs nao e permitida';
END IF;
 IF TG_OP = 'UPDATE' THEN
 IF NEW.tipo != OLD.tipo OR NEW.mensagem != OLD.mensagem OR NEW.data_hora !=
OLD.data_hora THEN
 RAISE EXCEPTION 'Apenas o campo email pode ser alterado em um log existente';
END IF;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;;;
CREATE OR REPLACE TRIGGER protege_logs
    BEFORE UPDATE OR DELETE ON logs
    FOR EACH ROW
    EXECUTE FUNCTION bloquear_alteracao_logs();;;
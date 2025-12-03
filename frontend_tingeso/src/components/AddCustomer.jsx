import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import {
    Box,
    Card,
    CardContent,
    Typography,
    TextField,
    Button,
    Alert,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    CircularProgress
} from "@mui/material";
import customerService from "../services/customer.service";

const AddCustomer = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        rut: '',
        email: '',
        phoneNumber: '',
        status: 'activo'
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    // Función para formatear RUT en tiempo real
    const formatRut = (value) => {
        const cleaned = value.replace(/[^0-9kK]/g, '');
        if (!cleaned) return '';

        // Limitar a máximo 9 caracteres (8 dígitos + 1 dígito verificador)
        const limited = cleaned.slice(0, 9);

        // Si solo hay un carácter, devolverlo tal cual
        if (limited.length === 1) return limited;

        // Separar cuerpo y dígito verificador
        const body = limited.slice(0, -1);
        const dv = limited.slice(-1).toUpperCase();

        // Formatear el cuerpo con puntos
        const formattedBody = body.replace(/\B(?=(\d{3})+(?!\d))/g, '.');

        // Retornar con guion desde que hay al menos 2 caracteres
        return `${formattedBody}-${dv}`;
    };

    const handleChange = (e) => {
        const { name, value } = e.target;

        // Si el campo es 'rut', aplicar formato
        if (name === 'rut') {
            setFormData(prev => ({
                ...prev,
                [name]: formatRut(value)
            }));
        } else {
            setFormData(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validación de campos obligatorios
        if (!formData.name || !formData.rut || !formData.email || !formData.phoneNumber) {
            setError('Por favor completa todos los campos obligatorios');
            return;
        }

        setLoading(true);
        setError('');

        try {
            // Pasar el objeto completo al servicio (con RUT formateado)
            await customerService.create(formData);

            alert('Cliente agregado exitosamente');
            navigate('/customers');
            // Limpiar formulario
            setFormData({
                name: '',
                rut: '',
                email: '',
                phoneNumber: '',
                status: 'activo'
            });

        } catch (err) {
            console.error('Error Completo:', err);

            let errorMessage = 'Error al guardar el cliente';

            if (err.response) {
                const data = err.response.data;
                if (typeof data === 'string') {
                    errorMessage = data;
                } else if (data.error) {
                    errorMessage = data.error;
                } else if (data.message) {
                    errorMessage = data.message;
                } else {
                    errorMessage = `Error: ${err.response.status} - ${err.response.statusText || 'Bad Request'}`;
                }
            } else if (err.request) {
                errorMessage = 'No se pudo conectar con el servidor';
            } else {
                errorMessage = err.message || 'Error al procesar la solicitud';
            }

            setError(errorMessage);

        } finally {
            setLoading(false);
        }
    };

    return (
        <Box sx={{ p: 3 }}>
            <Card>
                <CardContent sx={{ p: 4 }}>
                    <Typography
                        variant="h5"
                        gutterBottom
                        sx={{ color: '#0A142E', fontWeight: 'bold', mb: 3 }}
                    >
                        Agregar Nuevo Cliente
                    </Typography>

                    {error && (
                        <Alert severity="error" sx={{ mb: 3 }}>
                            {error}
                        </Alert>
                    )}

                    <Box
                        component="form"
                        onSubmit={handleSubmit}
                        sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}
                    >
                        <TextField
                            fullWidth
                            label="Nombre"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                        />

                        <TextField
                            fullWidth
                            label="RUT"
                            name="rut"
                            value={formData.rut}
                            onChange={handleChange}
                            placeholder="Ej: 12.345.678-9"
                            required
                        />

                        <TextField
                            fullWidth
                            type="email"
                            label="Email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />

                        <TextField
                            fullWidth
                            label="Teléfono"
                            name="phoneNumber"
                            value={formData.phoneNumber}
                            onChange={handleChange}
                            required
                        />

                        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 2 }}>
                            <Button
                                variant="outlined"
                                onClick={() => setFormData({
                                    name: '',
                                    rut: '',
                                    email: '',
                                    phoneNumber: '',
                                    status: 'activo'
                                })}
                                disabled={loading}
                            >
                                Limpiar
                            </Button>
                            <Button
                                type="submit"
                                variant="contained"
                                disabled={loading}
                                sx={{
                                    backgroundColor: '#0A142E',
                                    color: '#FACC15',
                                    '&:hover': {
                                        backgroundColor: '#1a2847'
                                    },
                                    '&:disabled': {
                                        backgroundColor: '#e0e0e0',
                                        color: '#9e9e9e'
                                    }
                                }}
                            >
                                {loading ? (
                                    <CircularProgress size={24} sx={{ color: '#FACC15' }} />
                                ) : (
                                    'Agregar Cliente'
                                )}
                            </Button>
                        </Box>
                    </Box>
                </CardContent>
            </Card>
        </Box>
    );
};

export default AddCustomer;
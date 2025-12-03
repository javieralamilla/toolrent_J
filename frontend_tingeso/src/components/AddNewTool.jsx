import React, { useState, useEffect } from 'react';
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
} from '@mui/material';
import { ArrowBack as ArrowBackIcon } from '@mui/icons-material';
import toolService from '../services/tool.service';
import categoryService from '../services/category.service';

const AddNewTool = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        categoryId: '', // SE guarda el ID
        replacementValue: '',
        rentalRate: '',
        quantity: 1
    });

    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(false);
    const[loadingCategories, setLoadingCategories] = useState(true);
    const [error, setError] = useState('');
    const[errorCategories, setErrorCategories] = useState('');

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                setLoadingCategories(true);
                const response = await categoryService.getAllCategories();
                setCategories(response.data.data || response.data);
                setErrorCategories('');
            } catch (error) {
                console.error('Error al cargar categorías:', error);
                setErrorCategories('Error al cargar las categorías');
                setCategories([]);
            } finally {
                setLoadingCategories(false);
            }
        };

        fetchCategories();
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async () => {
        // Validación de campos obligatorios
        if (!formData.name || !formData.categoryId) {
            setError('Por favor completa todos los campos obligatorios');
            return;
        }

        // Convertir a números para validar
        const quantity = parseInt(formData.quantity);
        const replacementValue = parseInt(formData.replacementValue);
        const rentalRate = parseInt(formData.rentalRate);

        // Validar que sean números válidos
        if (isNaN(quantity) || isNaN(replacementValue) || isNaN(rentalRate)) {
            setError('Los valores numéricos no son válidos');
            return;
        }

        // Validar que sean mayores a 0
        if (replacementValue <= 0 || rentalRate <= 0 || quantity < 1) {
            setError('Los valores numéricos deben ser mayores a 0');
            return;
        }

        setLoading(true);
        setError('');

        try {
            // Construir el objeto category correctamente
            const selectedCategory = categories.find(c => c.id === formData.categoryId);
            
            const toolData = {
                name: formData.name,
                category: {
                    id: selectedCategory.id,
                    name: selectedCategory.name
                }
            };



            await toolService.saveTool(
                toolData, 
                quantity,
                replacementValue,
                rentalRate
            );
            
            alert('Herramienta guardada exitosamente');
            navigate('/addTool');
            
        } catch (err) {
            console.error('Error completo:', err);
            
            let errorMessage = 'Error al guardar la herramienta';
            
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
            <Button
                startIcon={<ArrowBackIcon />}
                onClick={() => navigate('/addTool')}
                sx={{ 
                    mb: 3,
                    color: '#0A142E',
                    '&:hover': {
                        backgroundColor: 'rgba(10, 20, 46, 0.05)'
                    }
                }}
            >
                Volver a opciones
            </Button>

            <Card>
                <CardContent sx={{ p: 4 }}>
                    <Typography variant="h5" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold', mb: 3 }}>
                        Registrar Herramienta Nueva
                    </Typography>
                    
                    {error && (
                        <Alert severity="error" sx={{ mb: 3 }}>
                            {error}
                        </Alert>
                    )}
                    {errorCategories && (
                        <Alert severity="warning" sx={{ mb: 3 }}>
                            {errorCategories}
                        </Alert>
                    )}

                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                        <TextField
                            fullWidth
                            label="Nombre de la herramienta"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                        />

                        <FormControl fullWidth required>
                            <InputLabel id="category-label">Categoría</InputLabel>
                            <Select
                                labelId="category-label"
                                name="categoryId"
                                value={formData.categoryId}
                                onChange={handleChange}
                                label="Categoría"
                            >
                                <MenuItem value="">
                                    <em>-- Selecciona una categoría --</em>
                                </MenuItem>
                                {categories.map((cat) => (
                                    <MenuItem key={cat.id} value={cat.id}>
                                        {cat.name}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        <TextField
                            fullWidth
                            type="number"
                            label="Valor de reposición"
                            name="replacementValue"
                            value={formData.replacementValue}
                            onChange={handleChange}
                            required
                            inputProps={{ min: 0, step: "1" }}
                        />

                        <TextField
                            fullWidth
                            type="number"
                            label="Tarifa de renta diaria"
                            name="rentalRate"
                            value={formData.rentalRate}
                            onChange={handleChange}
                            required
                            inputProps={{ min: 0, step: "1" }}
                        />

                        <TextField
                            fullWidth
                            type="number"
                            label="Cantidad inicial"
                            name="quantity"
                            value={formData.quantity}
                            onChange={handleChange}
                            required
                            inputProps={{ min: 1 }}
                        />

                        <Alert severity="info">
                            Esta opción es para registrar una herramienta completamente nueva en el sistema.
                        </Alert>

                        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 2 }}>
                            <Button
                                variant="outlined"
                                onClick={() => navigate('/addTool')}
                                disabled={loading}
                            >
                                Cancelar
                            </Button>
                            <Button
                                variant="contained"
                                onClick={handleSubmit}
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
                                {loading ? <CircularProgress size={24} sx={{ color: '#FACC15' }} /> : 'Guardar Herramienta'}
                            </Button>
                        </Box>
                    </Box>
                </CardContent>
            </Card>
        </Box>
    );
};

export default AddNewTool;
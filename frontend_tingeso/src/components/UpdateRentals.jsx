import { useNavigate } from 'react-router-dom';
import {
    Box,
    Card,
    CardContent,
    Typography,
    Button,
    Grid,
    Paper
} from "@mui/material";
import {
    ArrowBack as ArrowBackIcon,
    AttachMoney as AttachMoneyIcon,
    LocalOffer as LocalOfferIcon,
    TrendingUp as TrendingUpIcon
} from "@mui/icons-material";

const UpdateRentals = () => {
    const navigate = useNavigate();

    const options = [
        {
            title: 'Actualizar Valor de Reposición',
            description: 'Modifica el valor de reposición de una herramienta específica',
            icon: <AttachMoneyIcon sx={{ fontSize: 48, color: '#0A142E' }} />,
            path: '/updateReplacementValue',
            color: '#0A142E'
        },
        {
            title: 'Actualizar Tarifa Diaria de Arriendo',
            description: 'Modifica la tarifa diaria de arriendo de una herramienta',
            icon: <LocalOfferIcon sx={{ fontSize: 48, color: '#0A142E' }} />,
            path: '/updateDailyRentalRate',
            color: '#0A142E'
        },
        {
            title: 'Actualizar Tarifa Global',
            description: 'Modifica el valor de la tarifa global de arriendo',
            icon: <TrendingUpIcon sx={{ fontSize: 48, color: '#0A142E' }} />,
            path: '/updateGlobalRates',
            color: '#0A142E'
        }
    ];

    const handleNavigate = (path) => {
        navigate(path);
    };

    return (
        <Box sx={{ p: 3 }}>
            <Button
                startIcon={<ArrowBackIcon />}
                onClick={() => navigate('/toolsInventory')}
                aria-label="Volver al inventario"
                sx={{
                    mb: 3,
                    color: '#0A142E',
                    '&:hover': {
                        backgroundColor: 'rgba(10, 20, 46, 0.05)'
                    }
                }}
            >
                Volver al inventario
            </Button>

            <Card sx={{ mb: 4 }}>
                <CardContent sx={{ p: 4 }}>
                    <Typography
                        variant="h4"
                        gutterBottom
                        sx={{ color: '#0A142E', fontWeight: 'bold', mb: 2 }}
                    >
                        Actualizar Valores de Tarifas
                    </Typography>
                    <Typography
                        variant="body1"
                        sx={{ color: '#666', mb: 1 }}
                    >
                        Selecciona qué tipo de valor deseas actualizar:
                    </Typography>
                </CardContent>
            </Card>

            <Grid container spacing={3}>
                {options.map((option, index) => (
                    <Grid item xs={12} sm={6} md={4} key={index}>
                        <Paper
                            elevation={2}
                            sx={{
                                p: 3,
                                display: 'flex',
                                flexDirection: 'column',
                                alignItems: 'center',
                                textAlign: 'center',
                                cursor: 'pointer',
                                transition: 'all 0.3s ease',
                                height: '100%',
                                '&:hover': {
                                    elevation: 8,
                                    boxShadow: '0 8px 16px rgba(10, 20, 46, 0.15)',
                                    transform: 'translateY(-4px)'
                                }
                            }}
                            onClick={() => handleNavigate(option.path)}
                        >
                            <Box sx={{ mb: 2 }}>
                                {option.icon}
                            </Box>
                            <Typography
                                variant="h6"
                                sx={{
                                    color: '#0A142E',
                                    fontWeight: 'bold',
                                    mb: 1
                                }}
                            >
                                {option.title}
                            </Typography>
                            <Typography
                                variant="body2"
                                sx={{ color: '#666', mb: 2, flex: 1 }}
                            >
                                {option.description}
                            </Typography>
                            <Button
                                variant="contained"
                                size="small"
                                sx={{
                                    backgroundColor: '#0A142E',
                                    color: '#FACC15',
                                    '&:hover': {
                                        backgroundColor: '#1a2847'
                                    },
                                    mt: 'auto'
                                }}
                                onClick={(e) => {
                                    e.stopPropagation();
                                    handleNavigate(option.path);
                                }}
                                aria-label={`Ir a ${option.title}`}
                            >
                                Ir
                            </Button>
                        </Paper>
                    </Grid>
                ))}
            </Grid>
        </Box>
    );
};

export default UpdateRentals;
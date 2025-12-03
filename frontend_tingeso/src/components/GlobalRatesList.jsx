import { useEffect, useState } from "react";
import {
    Box,
    Card,
    CardContent,
    Typography,
    TextField,
    Button,
    Alert,
    CircularProgress,
    Grid,
    FormControl,
    Select,
    MenuItem,
    Table,
    TableContainer,
    TableHead,
    TableRow,
    TableCell,
    TableBody,
    Paper,
    Chip
} from "@mui/material";
import FilterIcon from "@mui/icons-material/Filter";
import ClearIcon from "@mui/icons-material/Clear";
import globalRatesService from "../services/globalrates.service";

const GlobalRatesList = () => {
    const [globalRates, setGlobalRates] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const init = () => {
        setLoading(true);
        globalRatesService
            .getAll()
            .then((response) => {
                console.log("Mostrando tarifas globales:", response.data);

                //Ordenar tarifas por ID ascendentemente
                const sortedGlobalRates = response.data.sort((a, b) => a.id - b.id);

                setGlobalRates(sortedGlobalRates);
                setError(null);
            })
            .catch((error) => {
                console.log("Error obteniendo tarifas globales:", error);
                setError("Error obteniendo tarifas globales");
            })
            .finally(() => {
                setLoading(false);
            });
    };

    useEffect(() => {
        init();
    }, []);

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
                <CircularProgress />
            </Box>
        );
    }

    if (error && globalRates.length === 0) {
        return (
            <Box sx={{ p: 3 }}>
                <Alert severity="error">{error}</Alert>
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold' }}>
                Lista de Tarifas Globales
            </Typography>
            <Typography variant="body1" sx={{ mb: 3, color: '#666' }}>
                Aquí puedes ver todas las tarifas globales registradas en el sistema.
            </Typography>

            {error && (
                <Alert severity="warning" sx={{ mb: 3 }}>
                    {error}
                </Alert>
            )}

            {globalRates.length === 0 ? (
                <Alert severity="info">
                    No hay tarifas globales registradas
                </Alert>
            ) : (
                <TableContainer component={Paper} sx={{ mt: 2 }}>
                    <Table sx={{ minWidth: 650 }}>
                        <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
                            <TableRow>
                                <TableCell><strong>ID</strong></TableCell>
                                <TableCell><strong>Nombre tarifa</strong></TableCell>
                                <TableCell><strong>Valor de renta diaria</strong></TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {globalRates.map((globalRate) => (
                                <TableRow
                                    key={globalRate.id}
                                    sx={{ '&:hover': { backgroundColor: '#f9f9f9' } }}
                                >
                                    <TableCell>{globalRate.id}</TableCell>
                                    <TableCell>
                                        <Typography variant="body2" fontWeight="medium">
                                            {globalRate.rateName || 'Sin nombre'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>{globalRate.dailyRateValue || 'Sin valor'}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}
        </Box>
    );
};

export default GlobalRatesList;